# PetProtect

Protect tamed mobs from PvP.

![PetProtect Icon](src/main/resources/assets/petprotect/icon.png)

## Features

- Allows disabling tamed pet player damage and attacks

## Configuration
- `prevent_pet_damage`: Global toggle for the mod
- `prevent_pet_death`: Decides whether pets will be able to die under any circumstances
- `apply_totem_effects`: Decides whether Totem of Undying effects will be applied when preventing the death of a pet
- `prevent_pet_attack`: Decides whether pets will attack players
- `allow_owner_damage`: Decides whether owners should be able to damage (and kill!) their own pets regardless of other rules
- `ignore_creative`: Decides whether creative players can damage pets regardless of other rules

*The configuration (`config/petprotect.json`) is loaded on server start and there is currently no way to change or reload options without restarting.*