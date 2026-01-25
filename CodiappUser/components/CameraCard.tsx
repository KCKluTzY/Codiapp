import { View, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";

interface CameraCardProps {
    icon: keyof typeof Ionicons.glyphMap;
}

export default function CameraCard({ icon }: CameraCardProps) {
    return (
        <View style={styles.card}>
            <Ionicons name={icon} size={28} color="white" />
        </View>
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
