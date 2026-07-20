package lol.sylvie.petprotect.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import lol.sylvie.petprotect.Constants;
import org.jetbrains.annotations.Nullable;

import java.io.*;

public class Configuration {
    public static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private static final File FILE;
    static {
        FILE = new File("./config/" + Constants.MOD_ID + ".json");
        FILE.getParentFile().mkdirs();
    }


    // Anything hurts pet
    @SerializedName("prevent_pet_damage")
    public boolean preventPetDamage = true;

    // *Only* player hurts pet
    @SerializedName("only_prevent_players")
    public boolean onlyPreventPlayers = false;

    // Pet dies
    @SerializedName("prevent_pet_death")
    public boolean preventPetDeath = true;

    // Pet dies, but also with fire res and particles and stuff
    @SerializedName("apply_totem_effects")
    public boolean applyTotemEffects = false;

    // Pet hurts something
    @SerializedName("prevent_pet_attack")
    public boolean preventPetAttack = true;

    // Owner hurts pet
    @SerializedName("allow_owner_damage")
    public boolean allowOwnerDamage = false;

    // Creative player hurts pet
    @SerializedName("ignore_creative")
    public boolean ignoreCreative = true;

    public static @Nullable Configuration unsafeLoad() {
        try (FileReader reader = new FileReader(FILE)) {
            return GSON.fromJson(reader, Configuration.class);
        } catch (FileNotFoundException ignored) {}
        catch (IOException | JsonSyntaxException exception) {
            Constants.LOG.warn("Couldn't load {} config!", Constants.MOD_NAME, exception);
        }
        return null;
    }

    public static Configuration load() {
        Configuration config = unsafeLoad();
        if (config != null) return config;

        config = new Configuration();
        config.write();
        return config;
    }

    public void write() {
        try (FileWriter writer = new FileWriter(FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException writeException) {
            Constants.LOG.error("Couldn't write to config!", writeException);
        }
    }
}
