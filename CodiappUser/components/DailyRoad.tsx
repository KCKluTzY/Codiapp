import { Ionicons } from "@expo/vector-icons";
import { StyleSheet, Text, View } from "react-native";
import { Colors } from "../constants/Colors";

export default function DailyRoad() {
    return (
        {/* Carte synthétique du trajet quotidien + alerte de perturbation */}
        <View style={styles.dailyroadcard}>
            <View style={styles.topRow}>
                <Ionicons name="location-outline" size={48} color="#4b2dde" />
                <View style={styles.roadTextContainer}>
                    <Text style={styles.roadText}>Votre Trajet Habituel</Text>
                    <Text style={styles.subtitle}>Ligne 3 → Gare centrale</Text>
                </View>
            </View>

            <View style={styles.perturbationcard}>
                <Ionicons name="warning-outline" size={32} color="#de4b4b" />
                <Text style={styles.perturbationText}>Perturbation sur le chemin</Text>
            </View>
        </View>
    );
}
const styles = StyleSheet.create({
    dailyroadcard: {
        backgroundColor: Colors.primary,
        borderRadius: 24,
        padding: 30,
        borderColor: "#68b1ff",
        borderWidth: 5,
        marginBottom: 20,
    },
    topRow: {
        flexDirection: "row",
        alignItems: "center",
    },
    roadTextContainer: {
        flex: 1,
    },
    roadText: {
        color: "#0946a8",
        fontSize: 18,
        fontWeight: "500",
    },
    subtitle: {
        color: "#0946a8",
        fontSize: 14,
        fontWeight: "400",
    },
    perturbationcard: {
        marginTop: 20,
        flexDirection: "row",
        alignItems: "center",
        backgroundColor: "#ffe5e5",
        padding: 10,
        borderRadius: 12,
    },
    perturbationText: {
        color: Colors.danger,
        fontSize: 16,
        fontWeight: "400",
    },

});
