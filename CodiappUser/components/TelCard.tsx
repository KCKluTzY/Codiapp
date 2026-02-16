import { View, StyleSheet, Pressable } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";

interface TelCardProps {
    icon: keyof typeof Ionicons.glyphMap;
}

export default function TelCard({ icon }: TelCardProps) {
    const router = useRouter();
    return (
        // Petite carte/action téléphonique — lance l'appel à un aidant
        <Pressable onPress={() => router.push("/AppelAidant")} style={styles.card}>
            <View style={styles.card}>
                <Ionicons name={icon} size={28} color="white" />
            </View>
        </Pressable>
    );
}

const styles = StyleSheet.create({
    card: {
        flex: 1,
        backgroundColor: "#50ff3d",
        borderRadius: 16,
        paddingVertical: 20,
        marginHorizontal: 6,
        alignItems: "center",
        justifyContent: "center",
    },
});
