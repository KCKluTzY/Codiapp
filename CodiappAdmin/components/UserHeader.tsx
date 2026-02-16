import { View, Text, Pressable, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";

export default function UserHeader() {
    const router = useRouter();

    return (
        <View style={styles.header}>
            {/* Bouton back à gauche + titre centré */}
            <Pressable onPress={() => router.back()} hitSlop={12}>
                <Ionicons name="arrow-back" size={26} color="#111" />
            </Pressable>

            <Text style={styles.title}>Utilisateur</Text>

            <View style={{ width: 26 }} />
        </View>
    );
}

const styles = StyleSheet.create({
    header: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
        paddingHorizontal: 16,
        paddingVertical: 16,
        backgroundColor: "#fff",
    },
    title: {
        fontSize: 18,
        fontWeight: "700",
    },
});
