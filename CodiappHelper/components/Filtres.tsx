import { View, Text, Pressable, StyleSheet } from "react-native";
import { useState } from "react";

type FilterType = "urgent" | "all" | "map";

export default function Filtres() {
    const [active, setActive] = useState<FilterType>("urgent");

    return (
        <View style={styles.container}>
            <Pressable style={[styles.tab, active === "urgent" && styles.activeUrgent,]} onPress={() => setActive("urgent")}>
                <Text style={[styles.text, active === "urgent" && styles.activeText,]}>Urgent (0)</Text>
            </Pressable>

            <Pressable style={[styles.tab, active === "all" && styles.active]} onPress={() => setActive("all")}>
                <Text style={[styles.text, active === "all" && styles.activeText]}>Toutes</Text>
            </Pressable>

            <Pressable style={[styles.tab, active === "map" && styles.active]} onPress={() => setActive("map")}>
                <Text style={[styles.text, active === "map" && styles.activeText]}>Afficher Carte</Text>
            </Pressable>
        </View>
    );
}
const styles = StyleSheet.create({
    container: {
        flexDirection: "row",
        paddingHorizontal: 16,
        marginTop: 16,
        marginBottom: 16,
    },

    tab: {
        paddingVertical: 10,
        paddingHorizontal: 16,
        borderRadius: 999,
        backgroundColor: "#e3e3e3cd",
        marginRight: 10,
    },

    text: {
        fontSize: 16,
        fontWeight: "600",
        color: "#555",
    },

    active: {
        backgroundColor: "#EDE9FE",
    },

    activeUrgent: {
        backgroundColor: "#EF4444",
    },

    activeText: {
        color: "white",
    },
});
