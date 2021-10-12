package net.wesjd.anvilgui.version;

import com.crazicrafter1.gapi.Main;
import org.bukkit.Bukkit;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Matches the server's NMS version to its {@link VersionWrapper}
 *
 * @author Wesley Smith
 * @since 1.2.1
 */
public class VersionMatcher {

    /**
     * Matches the server version to it's {@link VersionWrapper}
     *
     * @return The {@link VersionWrapper} for this server
     * @throws IllegalStateException If the version wrapper failed to be instantiated or is unable to be found
     */
    public VersionWrapper match() {
        final String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1);

        try {
            //File myJar = new File(Main.getInstance().getDataFolder(), String.format("anvilgui-%s.jar", serverVersion));
            //URLClassLoader child = new URLClassLoader(
            //        new URL[] { myJar.toURI().toURL() },
            //        getClass().getClassLoader()); // ClassLoader.getSystemClassLoader()
            //return (VersionWrapper) Class.forName(getClass().getPackage().getName() + ".Wrapper" + serverVersion, true, child)
            //        .getConstructor().newInstance();

            return (VersionWrapper) Class.forName(getClass().getPackage().getName() + ".Wrapper" + serverVersion)
                    .getConstructor().newInstance();
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException exception) {
            throw new IllegalStateException("Failed to instantiate version wrapper for version " + serverVersion, exception);
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("AnvilGUI does not support server version \"" + serverVersion + "\"", exception);
        }
    }

}