package lol.sylvie.petprotect.platform;

import lol.sylvie.petprotect.platform.services.IPlatformHelper;

public class FabricPlatformHelper implements IPlatformHelper {
    @Override
    public String getPlatformName() {
        return "Fabric";
    }
}
