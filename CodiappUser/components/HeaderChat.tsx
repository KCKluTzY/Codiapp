import { View, Text, StyleSheet, Image, Pressable } from "react-native";
import { Colors } from "../constants/Colors";
import { Ionicons } from "@expo/vector-icons";
import StatsRow from "./IconeRow";
import { useRouter } from "expo-router";

export default function HeaderChat() {
    const router = useRouter();
    return (
        <View style={styles.container}>
            <View style={styles.topRow}>
                <Pressable onPress={() => router.push("/HelpScreenUser")} hitSlop={12}>
                    <Ionicons name="arrow-back" size={28} color="white" />
                </Pressable>
                <Text style={styles.demandeText}>Aide en cours</Text>
                <View style={{ width: 28 }} />
            </View>
            <StatsRow />
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
