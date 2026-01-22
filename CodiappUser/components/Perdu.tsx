import { MaterialIcons } from "@expo/vector-icons";
import { StyleSheet, Text, View } from "react-native";

export default function Perdu() {
    return (
        <View style={styles.perduCard}>
            <MaterialIcons name="location-off" size={40} color="red" />
            <View style={styles.perduTextContainer}>
                <Text style={styles.perduText}>Je suis Perdu(e)</Text>
            </View>
        </View>

    )
}
const styles = StyleSheet.create({
    perduCard: {
        backgroundColor: "#fff04da4",
        borderRadius: 24,
        padding: 15,
        borderColor: "#f8ea52",
        borderWidth: 5,
        flexDirection: "row",
        alignItems: "center",
        marginBottom: 10,
    },
    perduTextContainer: {
        flex: 1,
    },
    perduText: {
        color: "#000000",
        fontSize: 18,
        fontWeight: "500",
        marginRight: 10,
    }
});
