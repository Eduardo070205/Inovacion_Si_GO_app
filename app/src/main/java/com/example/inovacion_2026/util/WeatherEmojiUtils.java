package com.example.inovacion_2026.util;

public class WeatherEmojiUtils {

    // Agrega este método en tu Fragment o en una clase utilitaria (p.ej. WeatherUtils.java)

    public static String climaToEmoji(String descripcion) {
        if (descripcion == null) return "🌡️";
        String d = descripcion.toLowerCase();

        // Cielo despejado
        if (d.contains("cielo claro") || d.contains("clear sky"))       return "☀️";

        // Pocas nubes
        if (d.contains("pocas nubes") || d.contains("few clouds"))      return "🌤️";

        // Nubes dispersas
        if (d.contains("nubes dispersas") || d.contains("scattered"))   return "⛅";

        // Muy nublado / overcast
        if (d.contains("muy nublado") || d.contains("overcast"))        return "☁️";

        // Nublado / broken clouds
        if (d.contains("nublado") || d.contains("broken clouds"))       return "🌥️";

        // Nubes genérico
        if (d.contains("nubes") || d.contains("clouds"))                return "🌥️";

        // Lluvia ligera
        if (d.contains("lluvia ligera") || d.contains("light rain"))    return "🌦️";

        // Lluvia intensa
        if (d.contains("lluvia intensa") || d.contains("heavy rain"))   return "🌧️";

        // Lluvia genérico
        if (d.contains("lluvia") || d.contains("rain"))                 return "🌧️";

        // Tormenta
        if (d.contains("tormenta") || d.contains("thunderstorm"))       return "⛈️";

        // Nieve
        if (d.contains("nieve") || d.contains("snow"))                  return "❄️";

        // Neblina / niebla
        if (d.contains("neblina") || d.contains("niebla")
                || d.contains("mist") || d.contains("fog"))             return "🌫️";

        // Viento
        if (d.contains("viento") || d.contains("windy"))                return "💨";

        // Fallback
        return "🌡️";
    }

// ─── USO EN TU FRAGMENT ───────────────────────────────────────────────────────
//
//   txtClmH1.setText(climaToEmoji(horaData.getDescripcion()));
//   txtClmDia1.setText(climaToEmoji(diaData.getDescripcion()));
//
// ─────────────────────────────────────────────────────────────────────────────

}
