# SoundHelper
SoundHelper is a utility mod to gather sound data in Minecraft.  
The mod runs for Fabric 1.19.4.

## Commands
### /blocked_sounds
Sends a list of the blocked combinations.  
No arguments required.

### /block_sound <sound> <volume> <pitch>
Blocks all sounds that match the given combination.  
Sound is the only required argument, although it can be bypassed with a value of `none` or `null.`  
Volume and pitch can be bypassed with negative values.

If all values are excluded / bypassed, no sounds will be logged.

### /unblock_sounmd <sound> <volume> <pitch>
Unblocks the sound combination provided.  
Acts as a reverse of `/block_sound`, with the arguments matching the same conditions.
