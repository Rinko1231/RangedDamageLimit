Through the configuration file,
you can enable ranged damage protection for specific mobs,
which either caps the damage received at a specific value
or completely nullifies attacks
when they occur beyond a set distance.
Additionally, damage falloff can be configured for long-range attacks based on distance.


{
    "mobId": "Entity registry ID (e.g. 'minecraft:pig', 'twilightforest:kobold')",
    "protectionDistance": "Minimum distance (in blocks) required to trigger ranged damage protection mechanics",
    "damageCap": "Maximum damage allowed when attack originates beyond protectionDistance",
    "noAggroBeyondCertainDistance": "[Boolean] Whether to completely nullify attacks and prevent aggro beyond specified distance",
    "noAggroDistance": "Distance threshold for attack nullification (must be ≥ protectionDistance when enabled)"
}
