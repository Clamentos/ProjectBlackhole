package io.github.clamentos.blackhole.cache;

// sync issues?
public class CacheEntry {

    private int[] ids;
    private long expires_at;

    public CacheEntry(int[] ids, long expires_at) {

        this.ids = ids;
        this.expires_at = expires_at;
    }

    public int[] getids() {

        return(ids);
    }

    public void setIds(int[] ids) {

        this.ids = ids;
    }

    public boolean isExpired() {

        return(expires_at < System.currentTimeMillis());
    }
}
