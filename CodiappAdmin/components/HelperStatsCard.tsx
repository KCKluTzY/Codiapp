import { View, Text, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";

export default function HelperStatsCard() {
    return (
        <View style={styles.container}>
            {/* Section statistiques pour un aidant */}
            <Text style={styles.sectionTitle}>Statistiques</Text>

            <View style={styles.row}>
                <View style={styles.card}>
                    <Ionicons name="stats-chart" size={22} color="#4db5ff" />
                    <Text style={styles.value}>12</Text>
                    <Text style={styles.label}>Aides ce mois</Text>
                </View>

                <View style={styles.card}>
                    <Ionicons name="calendar" size={22} color="#4db5ff" />
                    <Text style={styles.value}>84</Text>
                    <Text style={styles.label}>Aides cette année</Text>
                </View>
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        padding: 16,
    },
    sectionTitle: {
        fontSize: 16,
        fontWeight: "700",
        marginBottom: 12,
    },
    row: {
        flexDirection: "row",
        gap: 12,
    },
    card: {
        flex: 1,
        backgroundColor: "#fff",
        borderRadius: 16,
        padding: 16,
        alignItems: "center",
    },
    value: {
        fontSize: 20,
        fontWeight: "800",
        marginTop: 8,
    },
    label: {
        fontSize: 12,
        color: "#666",
        marginTop: 4,
    },
});
