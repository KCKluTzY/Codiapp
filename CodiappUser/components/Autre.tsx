
import { StyleSheet, Text, View, Image, Pressable } from "react-native";
import { Colors } from "../constants/Colors";
import { useRouter } from "expo-router";

export default function Autre() {
    const router = useRouter();
    return (
        <Pressable onPress={() => router.push("/ChatScreen")}>
            <View style={styles.AutreCard} >
                <Image source={require("../assets/images/Autre.png")} style={styles.logo} />
                <View style={styles.AutreTextContainer}>
                    <Text style={styles.AutreText}>Autre chose</Text>
                </View>
            </View>
        </Pressable>

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
    logo: {
        width: 40,
        height: 40,
        marginRight: 10,
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