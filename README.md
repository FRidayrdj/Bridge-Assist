# bridge-assist-forge

A Forge 1.8.9 mod that sneaks at block edges and places blocks for you while you bridge.

> **History.** Written around 2020, when I was about 12/13, for the reason most of my code from that era exists: I kept falling off bridges in Bedwars. Uploaded in 2026 as an archive of how I started programming. Commit dates are the real ones.

## Disclaimer

Bridging assists are cheating on most servers, and this one is no exception. I'm not shipping a compiled version, and please don't use a build of this anywhere it's banned. I put the source up because the interesting part is the world-state logic, not the advantage it gives.

## What it does

Toggle with `K`. While it's on:

1. If there's no block under where you're about to be, and you're on the ground, it holds sneak for you so you stop at the edge.
2. If you're holding a block and looking at a block face, it right-clicks for you, with a two-tick cooldown so it doesn't spam.

If you're already holding sneak yourself, it leaves that alone.

## How the edge detection works

Every client tick, the mod takes your position and adds your motion vector multiplied by 3. That's a rough guess at where your feet will be in three ticks. It looks up the block one unit below that point. If the material there is air and you're on the ground, you're about to walk off something.

It's a cheap trick. No raycasting, no bounding boxes, just "where am I headed" and "is there floor there."

Hooks used: `InputEvent.KeyInputEvent` for the toggle, `ClientTickEvent` for everything else. The sneak is applied with `KeyBinding.setKeyBindState` and the placement by calling `KeyBinding.onTick` on the use-item key.

## What I learned

- Reading world state from the client (`theWorld.getBlockState`) and checking materials.
- Player motion fields, and the difference between where you are and where you're going.
- Faking input by poking key bindings instead of touching the keyboard.
- That `BlockPos` made from doubles floors them, which matters a lot for negative coordinates. I did not know this at the time and I'm not sure the original handles it.

## What I'd change now

- When the mod is toggled off mid-edge, the forced sneak isn't explicitly released. It probably fixes itself on the next real key event, but I wouldn't bet on it.
- The lookahead is zero when you're standing still, so it does nothing until you've already started moving.
- The 3x multiplier is a guess I never tuned. It should depend on speed.
- Placement should check the face being targeted, not just that the crosshair is on some block.
