import { MaterialIcons } from "@expo/vector-icons";
import { StyleSheet, Text, View } from "react-native";
import { Colors } from "../constants/Colors";

export default function Panne() {
    return (
        <View style={styles.PanneCard}>
            <MaterialIcons name="warning-amber" size={40} color={Colors.danger} />
            <View style={styles.PanneTextContainer}>
                <Text style={styles.PanneText}>Le Tram/bus est en panne</Text>
            </View>
        </View>

    )
}
const styles = StyleSheet.create({
    PanneCard: {
        backgroundColor: "#ff714da4",
        borderRadius: 24,
        padding: 15,
        borderColor: "#ff714df1",
        borderWidth: 5,
        flexDirection: "row",
        alignItems: "center",
        marginBottom: 10,
    },
    PanneTextContainer: {
        flex: 1,
    },
    PanneText: {
        color: "#000000",
        fontSize: 18,
        fontWeight: "500",
        marginRight: 10,
    }
});
