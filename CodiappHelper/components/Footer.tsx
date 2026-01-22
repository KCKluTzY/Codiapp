import { View, Text, Pressable, StyleSheet } from "react-native";
import { useState } from "react";
import { MaterialIcons } from "@expo/vector-icons"; // tu peux choisir une autre librairie

type FilterType = "demandes" | "Groupe" | "Aides en cours";

const filters: { key: FilterType; label: string; icon: string }[] = [
    { key: "demandes", label: "Demandes", icon: "error-outline" },
    { key: "Groupe", label: "Groupe", icon: "group" },
    { key: "Aides en cours", label: "Aides en cours", icon: "diversity-1" },
];

export default function Footer() {
    const [active, setActive] = useState<FilterType>("demandes");

    return (
        <View style={styles.container}>
            {filters.map((filter) => {
                const isActive = active === filter.key;
                return (
                    <Pressable
                        key={filter.key}
                        style={styles.tab}
                        onPress={() => setActive(filter.key)}
                    >
                        <MaterialIcons
                            name={filter.icon as any}
                            size={35}
                            color={isActive ? "#9f44ef" : "#555"}
                            style={{ marginBottom: 4, }}
                        />
                        <Text style={[styles.text, isActive && styles.activeText]}>
                            {filter.label}
                        </Text>
                    </Pressable>
                );
            })}
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        flexDirection: "row",
        justifyContent: "space-around", // espace égal entre les boutons
        paddingHorizontal: 16,
        paddingVertical: 12,
        borderTopWidth: 1,
        borderTopColor: "#e3e3e3",
    },

    tab: {
        alignItems: "center",
        padding: 8,
        marginLeft: 10,
    },

    text: {
        fontSize: 14,
        fontWeight: "600",
        color: "#555",
    },

    activeText: {
        color: "#9f44ef",
    },
});
