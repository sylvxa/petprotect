package lol.sylvie.petprotect.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import lol.sylvie.petprotect.PetProtect;

import java.io.*;

public class ConfigInstance {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    @SerializedName("prevent_pet_damage")
    boolean preventPetDamage = true; // direct melee (AttackEntityCallback)

    @SerializedName("prevent_indirect_pet_damage")
    boolean preventIndirectPetDamage = true; // new: projectiles / other indirect sources

    @SerializedName("prevent_pet_death")
    boolean preventPetDeath = true;

    @SerializedName("apply_totem_effects")
    boolean applyTotemEffects = false;

    @SerializedName("prevent_pet_attack")
    boolean preventPetAttack = true;

    @SerializedName("allow_owner_damage")
    boolean allowOwnerDamage = false;

    @SerializedName("ignore_creative")
    boolean ignoreCreative = true;

    public static ConfigInstance fromFile(File file) {
        try (FileReader reader = new FileReader(file)) {
            ConfigInstance instance = GSON.fromJson(reader, ConfigInstance.class);
            // Ensure new fields have defaults if absent
            if (instance == null) instance = new ConfigInstance();
            return instance;
        } catch (IOException | JsonSyntaxException readException) {
            PetProtect.LOGGER.warn("Couldn't load config (creating default).");
            ConfigInstance instance = new ConfigInstance();
            instance.writeToFile(file);
            return instance;
        }
    }

    public void writeToFile(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(this, writer);
        } catch (IOException writeException) {
            PetProtect.LOGGER.error("Couldn't write to config!", writeException);
        }
    }

    public boolean preventPetDamage() {
        return preventPetDamage;
    }

    public boolean preventIndirectPetDamage() {
        return preventIndirectPetDamage;
    }

    public boolean preventPetDeath() {
        return preventPetDeath;
    }

    public boolean applyTotemEffects() {
        return applyTotemEffects;
    }

    public boolean preventPetAttack() {
        return preventPetAttack;
    }

    public boolean allowOwnerDamage() {
        return allowOwnerDamage;
    }

    public boolean shouldIgnoreCreative() {
        return ignoreCreative;
    }
}
