import { View, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";

interface LocationCardProps {
    icon: keyof typeof Ionicons.glyphMap;
}

export default function LocationCard({ icon }: LocationCardProps) {
    return (
        <View style={styles.card}>
            <Ionicons name={icon} size={28} color="white" />
        </View>
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
