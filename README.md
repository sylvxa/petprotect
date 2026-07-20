# PetProtect

Protect tamed mobs from PvP.

![PetProtect Icon](fabric/src/main/resources/assets/petprotect/icon.png)

## Features

- Allows disabling tamed pet player damage and attacks

## Configuration
- `prevent_pet_damage`: Global toggle for the mod
- `only_prevent_players`: Decides if environmental/mob damage will affect pets or not
- `prevent_pet_death`: Decides whether pets will be able to die under any circumstances
- `apply_totem_effects`: Decides whether Totem of Undying effects will be applied when preventing the death of a pet
- `prevent_pet_attack`: Decides whether pets will attack players
- `allow_owner_damage`: Decides whether owners should be able to damage (and kill!) their own pets regardless of other rules
- `ignore_creative`: Decides whether creative players can damage pets regardless of other rules

*The configuration (`config/petprotect.json`) can be reloaded using the `/petprotect reload` command.*