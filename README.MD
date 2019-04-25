# deinSync

deinSync synchronizes player inventories and statistics between servers.

deinSync will immediately write any changes to a player's inventory to the database.
Unlike various other approaches, this one has established. deinSync is most likely the most
reliable inventory synchronization plugin out there. On the other side it can cause
a lot of traffic and load on the database. However, deinSync has been tested with 200
concurrent players and didn't cause any performance issues.

The plugin has been updated to support Minecraft 1.13.
