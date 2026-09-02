package net.kyrptonaught.quickshulker.util.update;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.kyrptonaught.quickshulker.QuickShulkerMod;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateChecker {

    private static final Logger LOGGER = LogManager.getLogger("Quick Shulker Update Checker");
    private static final String CURRENT_VERSION = getCurrentVersion();
    private static final String VERSION_LIST_URL = "https://api.github.com/repos/moranpcy/quickshulker/tags?per_page=100";
    private static final String DOWNLOAD_URL = "https://github.com/MoRanpcy/quickshulker";
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().executor(Util.getDownloadWorkerExecutor()).connectTimeout(Duration.ofSeconds(5)).build();
    private static UpdateInfo updateInfo;

    public static UpdateInfo checkForUpdates(boolean onCurrentThread){
        UpdateInfo info = getUpdateInfo();
        if(info != null) return info;
        return onCurrentThread ? checkWithCurrentThread() : checkWithNewThread();
    }

    private static UpdateInfo checkWithNewThread(){
        UpdateInfo info = null;
        ExecutorService executor = createCheckUpdateExecutor();
        try {
            info = executor.submit(UpdateChecker::checkWithCurrentThread).get();
        } catch (ExecutionException e) {
            LOGGER.error("Failed Quick Shulker update check!", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }finally {
            executor.shutdown();
        }
        return info;
    }

    private static UpdateInfo checkWithCurrentThread() {
        UpdateInfo info = null;
        try{
            info = updateInfo = checkUpdate0();
        } catch (IOException e) {
            LOGGER.error("Failed Quick Shulker update check!", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return info;
    }

    private static UpdateInfo checkUpdate() throws IOException, InterruptedException {
        String url = VERSION_LIST_URL;
        List<String> versions = new ArrayList<>();
        String[] curVersion = CURRENT_VERSION.split("-", 3);
        String modVersion = curVersion[0];
        String mcVersion = curVersion[1];
        while(url != null){
            HttpRequest.Builder request = HttpRequest.newBuilder().GET().uri(URI.create(url));
            HttpResponse<String> response = send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if(status != 200){
                LOGGER.warn("Mod version list responded with a non-200 status: {}!", status);
                return null;
            }
            Gson gson = new GsonBuilder().create();
            List<String> vers = null;
            try {
                vers = gson.fromJson(response.body(), new TypeToken<List<Version>>(){}).stream()
                        .map(Version::getFriendlyString)
                        .filter(name -> {
                            String[] name2 = name.split("-", 3);
                            return name2.length == 2 && mcVersion.equals(name2[1]);
                        })
                        .map(name -> name.split("-", 3)[0])
                        .toList();
            }catch(JsonSyntaxException e){
                LOGGER.warn("Received invalid data, aborting loader update check!");
                return null;
            }
            if(!vers.isEmpty()){
                versions.addAll(vers);
            }
            String link = response.headers().firstValue("Link").orElse("");
            Pattern pattern = Pattern.compile("<([^>]+)>; rel=\"next\"");
            Matcher matcher = pattern.matcher(link);
            url = matcher.find() ? matcher.group(1) : null;
        }
        if(versions.isEmpty()) return null;
        String lastVersion = versions.stream().max(Version::compareString).orElse(modVersion);
        if(isNewer(lastVersion, modVersion)){
            LOGGER.debug("Quick Shulker has a update available!");
            return new UpdateInfo(modVersion, lastVersion);
        }
        LOGGER.debug("Quick Shulker is up to date.");
        return null;
    }

    private static UpdateInfo checkUpdate0() throws IOException, InterruptedException {
        String url = VERSION_LIST_URL;
        String lastVersion = null;
        String[] curVersion = CURRENT_VERSION.split("-", 3);
        String modVersion = curVersion[0];
        String mcVersion = curVersion[1];
        while(url != null){
            HttpRequest.Builder request = HttpRequest.newBuilder().GET().uri(URI.create(url));
            HttpResponse<String> response = send(request, HttpResponse.BodyHandlers.ofString());
            int status = response.statusCode();
            if(status != 200){
                LOGGER.warn("Mod version list responded with a non-200 status: {}!", status);
                return null;
            }
            JsonElement data = JsonParser.parseString(response.body());
            if(!data.isJsonArray()){
                LOGGER.warn("Received invalid data, aborting loader update check!");
                return null;
            }
            for(JsonElement child : data.getAsJsonArray()){
                if(!(child instanceof JsonObject)) continue;
                JsonObject obj = child.getAsJsonObject();
                Optional<String> name = getString(obj, "name");
                if(name.isEmpty()) continue;
                String[] name2 = name.get().split("-", 3);
                if(name2.length == 2 && mcVersion.equals(name2[1])){
                    lastVersion = name2[0];
                    break;
                }
            }
            if(lastVersion == null){
                String link = response.headers().firstValue("Link").orElse("");
                Pattern pattern = Pattern.compile("<([^>]+)>; rel=\"next\"");
                Matcher matcher = pattern.matcher(link);
                url = matcher.find() ? matcher.group(1) : null;
            }else{
                url = null;
            }
        }
        if(lastVersion != null && isNewer(lastVersion, modVersion)){
            LOGGER.debug("Quick Shulker has a update available!");
            return new UpdateInfo(modVersion, lastVersion);
        }
        LOGGER.debug("Quick Shulker is up to date.");
        return null;
    }

    public static String getCurrentVersion() {
        return FabricLoader.getInstance().getModContainer(QuickShulkerMod.MOD_ID).get().getMetadata().getVersion().getFriendlyString();
    }

    public static Optional<String> getString(JsonObject obj, String key) {
        if(!obj.has(key)) return Optional.empty();
        JsonElement e = obj.get(key);
        if(!(e instanceof JsonPrimitive) || !((JsonPrimitive) e).isString()) return Optional.empty();
        return Optional.of(e.getAsString());
    }

    public static UpdateInfo getUpdateInfo(){
        return updateInfo;
    }

    public static boolean isNewer(String self, String other) {
        return Version.compareString(self, other) > 0;
    }

    private static <T> HttpResponse<T> send(HttpRequest.Builder request, HttpResponse.BodyHandler<T> handler) throws IOException, InterruptedException {
        request.timeout(Duration.ofSeconds(10));
        return HTTP_CLIENT.send(request.build(), handler);
    }

    private static <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest.Builder request, HttpResponse.BodyHandler<T> handler) throws IOException, InterruptedException {
        request.timeout(Duration.ofSeconds(10));
        return HTTP_CLIENT.sendAsync(request.build(), handler);
    }

    public static ExecutorService createCheckUpdateExecutor(){
        return Executors.newSingleThreadExecutor(r -> new Thread(r, "Quick Shulker Update Checker"));
    }

    public static class UpdateInfo {
        public String curVersion;
        public String lastVersion;

        public UpdateInfo(String curVersion, String lastVersion) {
            this.curVersion = curVersion;
            this.lastVersion = lastVersion;
        }

        public boolean isUpdateAvailable() {
            return true;
        }

        public Text getUpdateMessage() {
            return Text.translatable("key.quickshulker.update.updateText", Text.translatable("key.categories.quickshulker").formatted(Formatting.BOLD)).append(" ")
                    .append(Text.translatable(
                            "key.quickshulker.update.version",
                            Text.literal(this.lastVersion)
                                    .formatted(Formatting.GOLD, Formatting.UNDERLINE)
                                    .styled(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.translatable("key.quickshulker.update.check"))))
                                    .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, getDownloadLink())))
                    ));
        }

        public String getDownloadLink() {
            return UpdateChecker.DOWNLOAD_URL;
        }
    }

    public static void register() {
        ClientPlayConnectionEvents.JOIN.register((listener, sender, client) -> {
            if(QuickShulkerMod.getConfig().checkUpdate) {
                Util.getDownloadWorkerExecutor().execute(() -> {
                    UpdateInfo info = checkForUpdates(false);
                    if(info == null) return;
                    client.execute(() -> client.inGameHud.getChatHud().addMessage(info.getUpdateMessage()));
                });
            }
        });
    }
}
