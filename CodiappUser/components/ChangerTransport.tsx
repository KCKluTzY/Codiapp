import { MaterialIcons } from "@expo/vector-icons";
import { StyleSheet, Text, View } from "react-native";
import { Colors } from "../constants/Colors";

export default function ChangerTransport() {
    return (
        <View style={styles.ChangerTransportCard} >
            <MaterialIcons name="warning-amber" size={40} color={Colors.danger} />
            <View style={styles.ChangerTransportTextContainer}>
                <Text style={styles.ChangerTransportText}>Je dois changer de transport</Text>
            </View>
        </View>

    )
}
const styles = StyleSheet.create({
    ChangerTransportCard: {
        backgroundColor: Colors.button_background,
        borderRadius: 24,
        padding: 15,
        borderColor: Colors.button_border,
        borderWidth: 5,
        flexDirection: "row",
        alignItems: "center",
        marginBottom: 10,
    },
    ChangerTransportTextContainer: {
        flex: 1,
    },
    ChangerTransportText: {
        color: "#000000",
        fontSize: 18,
        fontWeight: "500",
        marginRight: 10,
    }
});