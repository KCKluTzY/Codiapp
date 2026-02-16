import { MaterialIcons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useState } from "react";
import { Pressable, StyleSheet, Text, View } from "react-native";

// 1. On définit les types pour que HomeScreen et Footer se comprennent
export type FilterType = "Demandes" | "Groupe" | "Aides en cours";

interface FooterProps {
    onTabChange?: (tab: "demandes" | "aides") => void; // Prop pour changer la liste sur HomeScreen
}

const filters: { key: FilterType; label: string; icon: string }[] = [
    { key: "Demandes", label: "Demandes", icon: "error-outline" },
    { key: "Groupe", label: "Groupe", icon: "group" },
    { key: "Aides en cours", label: "Aides en cours", icon: "diversity-1" },
];

export default function Footer({ onTabChange }: FooterProps) {
    const [active, setActive] = useState<FilterType>("Demandes");
    const router = useRouter();

    return (
        <View style={styles.container}>
            {/* Footer bas : navigation rapide entre vues (peut déclencher navigation ou callback) */}
            {filters.map((filter) => {
                const isActive = active === filter.key;
                return (
                    <Pressable
                        key={filter.key}
                        style={styles.tab}
                        onPress={() => {
                            setActive(filter.key);

                            // LOGIQUE DE NAVIGATION / CHANGEMENT
                            if (filter.key === "Groupe") {
                                router.push("/ChatScreen");
                            } else if (filter.key === "Demandes") {
                                // On dit au HomeScreen d'afficher les demandes
                                if (onTabChange) onTabChange("demandes");
                            } else if (filter.key === "Aides en cours") {
                                // On dit au HomeScreen d'afficher les aides acceptées
                                if (onTabChange) onTabChange("aides");
                            }
                        }}
                    >
                        <MaterialIcons
                            name={filter.icon as any}
                            size={35}
                            color={isActive ? "#9f44ef" : "#555"}
                            style={{ marginBottom: 4 }}
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
        justifyContent: "space-around",
        paddingHorizontal: 16,
        paddingVertical: 12,
        borderTopWidth: 1,
        borderTopColor: "#e3e3e3",
        backgroundColor: "#fff", // Important pour que le footer ne soit pas transparent
    },
    tab: {
        alignItems: "center",
        flex: 1, // On utilise flex 1 pour que les boutons soient bien répartis
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