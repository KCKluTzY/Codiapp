import { StyleSheet, Text, View, Image, Pressable } from "react-native";
import { Colors } from "../constants/Colors";
import { useRouter } from "expo-router"

export default function Derange() {
    const router = useRouter();
    return (
        <Pressable onPress={() => router.push("/AppelAidant")}>
            <View style={styles.DerangeCard} >
                <Image source={require("../assets/images/derange.png")} style={styles.logo} />
                <View style={styles.DerangeTextContainer}>
                    <Text style={styles.DerangeText}>Quelqu'un me dérange</Text>
                </View>
            </View>
        </Pressable>

    )
}
const styles = StyleSheet.create({
    DerangeCard: {
        backgroundColor: Colors.button_background,
        borderRadius: 24,
        padding: 15,
        borderColor: Colors.button_border,
        borderWidth: 5,
        flexDirection: "row",
        alignItems: "center",
        marginBottom: 10,
    },
    DerangeTextContainer: {
        flex: 1,
    },
    DerangeText: {
        color: "#000000",
        fontSize: 18,
        fontWeight: "500",
        marginRight: 10,
    },
    logo: {
        width: 40,
        height: 40,
        marginRight: 10,
    }
});