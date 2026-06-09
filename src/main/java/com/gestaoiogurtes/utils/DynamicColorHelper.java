package com.gestaoiogurtes.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DynamicColorHelper {

    private final Map<String, String> colorMap = new HashMap<>();
    private final Random random = new Random();
    
    // Vibrant colors that work well with white text
    private static final String[] COLORS = {
        "#3b82f6", // blue-500
        "#10b981", // emerald-500
        "#f59e0b", // amber-500
        "#ef4444", // red-500
        "#8b5cf6", // violet-500
        "#ec4899", // pink-500
        "#14b8a6", // teal-500
        "#f97316", // orange-500
        "#06b6d4", // cyan-500
        "#6366f1", // indigo-500
        "#84cc16", // lime-500
        "#d946ef", // fuchsia-500
        "#f43f5e", // rose-500
        "#0ea5e9", // sky-500
        "#047857", // emerald-700
        "#b45309", // amber-700
        "#be123c", // rose-700
        "#4338ca", // indigo-700
        "#0f766e"  // teal-700
    };

    public String getColorForType(String typeName) {
        if (typeName == null || typeName.isBlank() || typeName.equalsIgnoreCase("Sem Tipo") || typeName.equalsIgnoreCase("Sem tipo")) {
            return "#9ca3af"; // gray-400 for empty or unknown types
        }
        
        return colorMap.computeIfAbsent(typeName, k -> {
            int index = random.nextInt(COLORS.length);
            return COLORS[index];
        });
    }
}
