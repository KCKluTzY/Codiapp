import { StyleSheet, Text, View, Image } from "react-native";
import { Colors } from "../constants/Colors";

export default function Blesse() {
    return (
        <View style={styles.BlesseCard} >
            <Image source={require("../assets/images/blesse.png")} style={styles.logo} />
            <View style={styles.BlesseTextContainer}>
                <Text style={styles.BlesseText}>Je suis blessé(e)</Text>
            </View>
        </View>

    )
}
const styles = StyleSheet.create({
    BlesseCard: {
        backgroundColor: Colors.button_background,
        borderRadius: 24,
        padding: 15,
        borderColor: Colors.button_border,
        borderWidth: 5,
        flexDirection: "row",
        alignItems: "center",
        marginBottom: 10,
    },
    BlesseTextContainer: {
        flex: 1,
    },
    BlesseText: {
        color: "#000000",
        fontSize: 18,
        fontWeight: "500",
        marginRight: 10,
    },
    logo: {
        width: 40,
        height: 40,
        marginRight: 10,
    },
});