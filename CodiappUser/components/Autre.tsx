
import { StyleSheet, Text, View } from "react-native";
import { Colors } from "../constants/Colors";
import MaterialIcons from "@expo/vector-icons/build/MaterialIcons";

export default function Autre() {
    return (
        <View style={styles.AutreCard} >
            <MaterialIcons name="warning-amber" size={40} color={Colors.danger} />
            <View style={styles.AutreTextContainer}>
                <Text style={styles.AutreText}>Autre chose</Text>
            </View>
        </View>

    )
}
const styles = StyleSheet.create({
    AutreCard: {
        backgroundColor: Colors.button_background,
        borderRadius: 24,
        padding: 15,
        borderColor: Colors.button_border,
        borderWidth: 5,
        flexDirection: "row",
        alignItems: "center",
        marginBottom: 10,
    },
    AutreTextContainer: {
        flex: 1,
    },
    AutreText: {
        color: "#000000",
        fontSize: 18,
        fontWeight: "500",
        marginRight: 10,
    }
});