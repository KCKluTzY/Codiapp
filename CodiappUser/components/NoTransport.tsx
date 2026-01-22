import { MaterialIcons } from "@expo/vector-icons";
import { StyleSheet, Text, View } from "react-native";

export default function NoTransport() {
    return (
        <View style={styles.noTransportCard}>
            <MaterialIcons name="departure-board" size={40} color="blue" />
            <View style={styles.noTransportTextContainer}>
                <Text style={styles.noTransportText}>Mon Transport n'est pas arrivé</Text>
            </View>
        </View>

    )
}
const styles = StyleSheet.create({
    noTransportCard: {
        backgroundColor: "#4da9ffa4",
        borderRadius: 24,
        padding: 15,
        borderColor: "#4da9ffa4",
        borderWidth: 5,
        flexDirection: "row",
        alignItems: "center",
        marginBottom: 10,
    },
    noTransportTextContainer: {
        flex: 1,
    },
    noTransportText: {
        color: "#000000",
        fontSize: 18,
        fontWeight: "500",
        marginRight: 10,
    }
});
