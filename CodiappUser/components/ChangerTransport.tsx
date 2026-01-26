import { StyleSheet, Text, View, Image, Pressable } from "react-native";
import { Colors } from "../constants/Colors";
import { useRouter } from "expo-router";

export default function ChangerTransport() {
    const router = useRouter();
    return (
        <Pressable onPress={() => router.push("/ChatScreen")}>
            <View style={styles.ChangerTransportCard} >
                <Image source={require("../assets/images/ChangerTransport.png")} style={styles.logo} />
                <View style={styles.ChangerTransportTextContainer}>
                    <Text style={styles.ChangerTransportText}>Je dois changer de transport</Text>
                </View>
            </View>
        </Pressable>
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
    logo: {
        width: 40,
        height: 40,
        marginRight: 10,
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