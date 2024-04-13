package ru.whitebeef.beefcore.command;

public class Alias {

    public static Alias of(String name) {
        return of(name, true);
    }

    public static Alias of(String name, boolean show) {
        return new Alias(name, show);
    }

    private final String name;
    private final boolean show;

    private Alias(String name, boolean show) {
        this.name = name.toLowerCase();
        this.show = show;
    }

    public String getName() {
        return name;
    }

    public boolean isShown() {
        return show;
    }
}
