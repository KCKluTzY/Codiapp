import { Ionicons } from "@expo/vector-icons";
import { StyleSheet, Text, View } from "react-native";
import { Colors } from "../constants/Colors";

export default function StatusCard() {
    return (
        // Carte de statut affichant la disponibilité des aidants
        <View style={styles.statusCard}>
            <Ionicons name="checkmark-circle-outline" size={48} color="#09a843" />
            <View style={styles.statusTextContainer}>
                <Text style={styles.statusText}>Vos aidants sont disponibles</Text>
            </View>
        </View>

    )
}
const styles = StyleSheet.create({
    statusCard: {
        backgroundColor: Colors.success,
        borderRadius: 24,
        padding: 15,
        borderColor: "#68ff9f",
        borderWidth: 5,
        flexDirection: "row",
        alignItems: "center",
        marginBottom: 10,
    },
    statusTextContainer: {
        flex: 1,
    },
    statusText: {
        color: "#09a843",
        fontSize: 18,
        fontWeight: "500",
    }
});
