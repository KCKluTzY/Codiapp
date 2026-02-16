import { View, StyleSheet, Pressable } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";

interface CameraCardProps {
    icon: keyof typeof Ionicons.glyphMap;
}

export default function CameraCard({ icon }: CameraCardProps) {
    const router = useRouter();
    return (
        // Petite carte menant à l'interface caméra
        <Pressable onPress={() => router.push("/CameraInterface")} style={styles.card}>
            <View>
                <Ionicons name={icon} size={28} color="white" />
            </View>
        </Pressable>
    );
}

const styles = StyleSheet.create({
    card: {
        flex: 1,
        backgroundColor: "#ab3dff",
        borderRadius: 16,
        paddingVertical: 20,
        marginHorizontal: 6,
        alignItems: "center",
        justifyContent: "center",
    },
});
