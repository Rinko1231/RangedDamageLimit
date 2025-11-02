package com.rinko1231.rangeddamagelimit.config;

public class MobProtectionRule {

    private String mobId;
    private double protectionDistance;
    private double damageCap;
    private boolean noAggroBeyondCertainDistance;
    private double noAggroDistance;

    public MobProtectionRule() {}

    // Getter和Setter
    public String getMobId() { return mobId; }
    public void setMobId(String mobId) { this.mobId = mobId; }
    public double getProtectionDistance() { return protectionDistance; }
    public void setProtectionDistance(double protectionDistance) { this.protectionDistance = protectionDistance; }
    public double getDamageCap() { return damageCap; }
    public void setDamageCap(double damageCap) { this.damageCap = damageCap; }
    public boolean getNoAggroBeyondCertainDistance() {return noAggroBeyondCertainDistance;}
    public void setNoAggroBeyondCertainDistance (boolean noAggroBeyondCertainDistance) { this.noAggroBeyondCertainDistance = noAggroBeyondCertainDistance;}
    public double getNoAggroDistance() {return noAggroDistance;}
    public void setNoAggroDistance(double noAggroDistance) {this.noAggroDistance = noAggroDistance;}
}