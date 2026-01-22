import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { Pressable, StyleSheet, Text } from "react-native";
import { Colors } from "../constants/Colors";

export default function EmergencyButton() {
    const router = useRouter();
    return (
        <Pressable style={styles.button} onPress={() => router.push("/HelpScreenUser")}>
            <Ionicons name="alert-circle-outline" size={96} color="white" />
            <Text style={styles.text}>J'AI BESOIN D'AIDE</Text>
        </Pressable>
    );
}

const styles = StyleSheet.create({
    button: {
        backgroundColor: Colors.danger,
        borderRadius: 24,
        padding: 80,
        alignItems: "center",
        marginBottom: 10,
    },
    text: {
        color: "#fff",
        fontSize: 35,
        fontWeight: "800",
        textAlign: "center",
    }
});
