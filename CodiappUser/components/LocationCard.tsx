import { View, StyleSheet, Pressable } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";

interface LocationCardProps {
    icon: keyof typeof Ionicons.glyphMap;
}

export default function LocationCard({ icon }: LocationCardProps) {
    const router = useRouter();
    return (
        // Carte d'action vers la carte (MapInterface)
        <Pressable onPress={() => router.push("/MapInterface")} style={styles.card}>
            <View>
                <Ionicons name={icon} size={28} color="white" />
            </View>
        </Pressable >
    );
}

const styles = StyleSheet.create({
    card: {
        flex: 1,
        backgroundColor: "#ffb83d",
        borderRadius: 16,
        paddingVertical: 20,
        marginHorizontal: 6,
        alignItems: "center",
        justifyContent: "center",
    },
});
