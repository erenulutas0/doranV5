package com.microservices.inventory.Model;

import java.util.List;

public enum Location {
    BESIKTAS(List.of("LEVENT", "ETILER", "ORTAKOY", "BESIKTAS_MERKEZ")),
    KADIKOY(List.of("MODA", "FENERBAHCE", "SUADIYE", "CADDEBOSTAN")),
    SISLI(List.of("NISANTASI", "MECIDIYEKOY", "OKMEYDANI")),
    ATASEHIR(List.of("ATASEHIR_MERKEZ", "KAYISDAGI", "ICERENKOY")),
    USKUDAR(List.of("ALTUNIZADE", "CUSINDEGIL", "KISIKLI", "BEYLERBEYI")),
    SARIYER(List.of("MASLAK", "ZEKERIYAKOY", "TARABYA", "ISTINYE")),
    MALTEPE(List.of("KÜÇÜKYALI", "ZÜMRÜTEVLER", "BÜYÜKBAKKALKÖY")),
    PENDIK(List.of("KURTKOY", "ESENYALI", "KAVAKPINAR")),
    BAKIRKOY(List.of("FLORYA", "YESILKOY", "ATAKOY")),
    BAGCILAR(List.of("GUNESLI", "MAHMUTBEY", "YAVUZSELIM")),
    BASAKSEHIR(List.of("IKITELLI", "BAŞAK", "ALTINSEHIR")),
    FATIH(List.of("AKSARAY", "CERRAHPAŞA", "SULTANAHMET"));

    private final List<String> subLocations;

    Location(List<String> subLocations) {
        this.subLocations = subLocations;
    }

    public List<String> getSubLocations() {
        return subLocations;
    }
}
