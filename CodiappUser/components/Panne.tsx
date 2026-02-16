
import { StyleSheet, Text, View, Image, Pressable } from "react-native";
import { Colors } from "../constants/Colors";
import { useRouter } from "expo-router";

export default function Panne() {
    const router = useRouter();
    return (
        // Option 'Panne' : signale une panne et ouvre le chat pour assistance
        <Pressable onPress={() => router.push("/ChatScreen")}>
            <View style={styles.PanneCard}>
                <Image source={require("../assets/images/Panne.png")} style={styles.logo} />
                <View style={styles.PanneTextContainer}>
                    <Text style={styles.PanneText}>Le tram/bus est en panne</Text>
                </View>
            </View>
        </Pressable>
    )
}
const styles = StyleSheet.create({
    PanneCard: {
        backgroundColor: Colors.button_background,
        borderRadius: 24,
        padding: 15,
        borderColor: Colors.button_border,
        borderWidth: 5,
        flexDirection: "row",
        alignItems: "center",
        marginBottom: 10,
    },
    logo: {
        width: 40,
        height: 40,
        marginRight: 10,
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
