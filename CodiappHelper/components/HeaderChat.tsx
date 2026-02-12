import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { Pressable, StyleSheet, Text, View } from "react-native";

export default function HeaderChat() {
    const router = useRouter();
    return (
        <View style={styles.container}>
            <View style={styles.topRow}>
                <Pressable onPress={() => router.push("../home/HomeScreenHelper")} hitSlop={12}>
                    <Ionicons name="arrow-back" size={28} color="white" />
                </Pressable>
                <Text style={styles.demandeText}>Chat</Text>
                <View style={{ width: 28 }} />
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        backgroundColor: "#4d82ff",
        paddingTop: 48,
        paddingHorizontal: 20,
        paddingBottom: 24,
        borderBottomLeftRadius: 24,
        borderBottomRightRadius: 24,
    },

    topRow: {
        flexDirection: "row",
        alignItems: "center",
        marginBottom: 20,
    },

    demandeText: {
        color: "white",
        fontSize: 20,
        fontWeight: "700",
        flex: 1,
        textAlign: "center",
    },

});
