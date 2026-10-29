package core.items;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ScanResult;

import core.machine.state.ItemType;

import static core.app.Vars.assets;

/** Discovers custom items once at startup and stores one instance per texture type. */
public final class Items {
    private static final EnumMap<ItemType, ItemBase> items =
        new EnumMap<>(ItemType.class);
    private static final Collection<ItemBase> allItems =
        Collections.unmodifiableCollection(items.values());

    private Items() {}

    public static void load() {
        EnumMap<ItemType, Class<? extends ItemBase>> classes = discoverItemClasses();
        EnumMap<ItemType, ItemBase> loaded = new EnumMap<>(ItemType.class);

        for (ItemType type : ItemType.values()) {
            Class<? extends ItemBase> itemClass = classes.get(type);
            ItemBase item = itemClass == null ? new ItemBase() : create(itemClass);
            item.name = type.getName();
            item.region = assets.getRegion(type);
            loaded.put(type, item);
        }

        // Leave the previous registry intact if discovery or initialization fails.
        items.clear();
        items.putAll(loaded);
    }

    private static EnumMap<ItemType, Class<? extends ItemBase>> discoverItemClasses() {
        EnumMap<ItemType, Class<? extends ItemBase>> classes = new EnumMap<>(ItemType.class);

        try (ScanResult scan = new ClassGraph()
                .enableClasspath()
                .enableClassInfo()
                .enableAnnotationInfo()
                .acceptPackages("core.items")
                .scan()) {
            for (ClassInfo info : scan.getClassesWithAnnotation(ItemTexture.class.getName())) {
                Class<?> candidate = Class.forName(info.getName(), false, Items.class.getClassLoader());
                if (!ItemBase.class.isAssignableFrom(candidate)
                    || Modifier.isAbstract(candidate.getModifiers())
                    || !Modifier.isPublic(candidate.getModifiers())) {
                    throw new IllegalStateException("@ItemTexture requires a public, concrete ItemBase: "
                        + candidate.getName());
                }

                Class<? extends ItemBase> itemClass = candidate.asSubclass(ItemBase.class);
                itemClass.getConstructor();
                ItemType texture = itemClass.getDeclaredAnnotation(ItemTexture.class).value();
                Class<? extends ItemBase> previous = classes.putIfAbsent(texture, itemClass);
                if (previous != null) {
                    throw new IllegalStateException("Duplicate item texture " + texture + ": "
                        + previous.getName() + " and " + itemClass.getName());
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException exception) {
            throw new IllegalStateException("Cannot discover item implementations", exception);
        }

        return classes;
    }

    private static ItemBase create(Class<? extends ItemBase> itemClass) {
        try {
            return itemClass.getConstructor().newInstance();
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot create item " + itemClass.getName(), exception);
        }
    }

    public static boolean isRegistered(ItemType type) {
        return items.containsKey(type);
    }

    public static ItemBase get(ItemType type) {
        return items.get(type);
    }

    /** Read-only view in ItemType declaration order. */
    public static Collection<ItemBase> getAllItems() {
        return allItems;
    }
}
