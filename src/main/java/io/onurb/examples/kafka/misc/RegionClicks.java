package io.onurb.examples.kafka.misc;

public class RegionClicks {

    public final String region;
    public final long clicks;

    public RegionClicks(String region, Long clicks) {
        this.region = region;
        this.clicks = clicks;
    }
}
