import { useRouter } from "expo-router";
import { Colors } from "@/constants/Colors";
import { StyleSheet, Text, View, Image, Pressable } from "react-native";

export default function Perdu() {
    const router = useRouter();
    return (
        <Pressable onPress={() => router.push("/ChatScreen")}>
            <View style={styles.perduCard}>
                <Image source={require("../assets/images/perdu.png")} style={styles.logo} />
                <View style={styles.perduTextContainer}>
                    <Text style={styles.perduText}>Je suis perdu(e)</Text>
                </View>
            </View>
        </Pressable>

    )
}
const styles = StyleSheet.create({
    perduCard: {
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
