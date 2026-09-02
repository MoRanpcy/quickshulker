package net.kyrptonaught.quickshulker.util.update;

import org.jspecify.annotations.NonNull;

public class Version implements Comparable<Version> {
    private final String name;

    public Version(String name){
        this.name = name;
    }

    public String getFriendlyString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Version)) return false;
        if(this == obj) return true;
        return this.name.equals(((Version) obj).name);
    }

    @Override
    public int compareTo(@NonNull Version o) {
        String[] self = this.name.split("-", 3);
        String[] other = o.getFriendlyString().split("-", 3);
        int result = compareString(self[1], other[1]);
        if(result == 0) return compareString(self[0], other[0]);
        return result;
    }

    public static int compareString(String self, String other){
        String[] str1 = self.split("\\.", 3);
        String[] str2 = other.split("\\.", 3);
        int len = Math.max(str1.length, str2.length);
        for(int i = 0; i < len; i++){
            int x = i < str1.length ? Integer.parseInt(str1[i]) : 0;
            int y = i < str2.length ? Integer.parseInt(str2[i]) : 0;
            if(x != y) return Integer.compare(x, y);
        }
        return 0;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
