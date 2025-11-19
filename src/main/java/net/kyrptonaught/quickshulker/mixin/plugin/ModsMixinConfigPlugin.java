package net.kyrptonaught.quickshulker.mixin.plugin;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import me.fallenbreath.conditionalmixin.api.checker.RestrictionChecker;
import me.fallenbreath.conditionalmixin.api.checker.RestrictionCheckers;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class ModsMixinConfigPlugin implements IMixinConfigPlugin {

    private final RestrictionChecker restrictionChecker = RestrictionCheckers.memorized();

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return restrictionChecker.checkRestriction(mixinClassName);
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void onLoad(String mixinPackage) {
        MixinExtrasBootstrap.init();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
