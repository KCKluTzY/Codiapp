import { useRouter } from "expo-router";
import { Pressable, StyleSheet, Text, View } from "react-native";

type FilterType = "urgent" | "all" | "map";

interface FiltresProps {
    active: FilterType;
    setActive: (filter: FilterType) => void;
    urgentCount: number;
}

export default function Filtres({ active, setActive, urgentCount }: FiltresProps) {
    const router = useRouter();

    return (
        <View style={styles.container}>
            {/* Bouton Toutes */}
            <Pressable
                style={[styles.tab, active === "all" && styles.activeAll]}
                onPress={() => setActive("all")}
            >
                <Text style={[styles.text, active === "all" && styles.activeText]}>
                    Toutes
                </Text>
            </Pressable>

            {/* Bouton Urgent */}
            <Pressable
                style={[styles.tab, active === "urgent" && styles.activeUrgent]}
                onPress={() => setActive("urgent")}
            >
                <Text style={[styles.text, active === "urgent" && styles.activeText]}>
                    Urgent ({urgentCount})
                </Text>
            </Pressable>

            {/* Bouton Carte */}
            <Pressable
                style={[styles.tab, active === "map" && styles.active]}
                onPress={() => {
                    setActive("map");
                    router.push("/MapInterface");
                }}
            >
                <Text style={[styles.text, active === "map" && styles.activeTextMap]}>
                    Afficher carte
                </Text>
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
    activeAll: {
        backgroundColor: "#9f44ef", // Violet pour "Toutes"
    },
    activeUrgent: {
        backgroundColor: "#EF4444", // Rouge pour "Urgent"
    },
    active: {
        backgroundColor: "#EDE9FE", // Couleur soft pour la carte
    },
    activeText: {
        color: "white",
    },
    activeTextMap: {
        color: "#9f44ef",
    }
});