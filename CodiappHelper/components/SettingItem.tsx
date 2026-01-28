import { Pressable, View, Text, StyleSheet } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { ReactNode } from "react";

interface SettingItemProps {
    icon: keyof typeof Ionicons.glyphMap;
    label: string;
    right?: ReactNode;
    onPress?: () => void;
}

export default function SettingItem({
    icon,
    label,
    right,
    onPress,
}: SettingItemProps) {
    const isPressable = !!onPress;

    return (
        <Pressable
            disabled={!isPressable}
            onPress={onPress}
            style={({ pressed }) => [
                styles.row,
                pressed && isPressable && styles.pressed,
            ]}
        >
            <View style={styles.left}>
                <Ionicons name={icon} size={22} color="#4db5ff" />
                <Text style={styles.label}>{label}</Text>
            </View>

            {right ? (
                right
            ) : isPressable ? (
                <Ionicons name="chevron-forward" size={20} color="#999" />
            ) : null}
        </Pressable>
    );
}


const styles = StyleSheet.create({
    row: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
        paddingHorizontal: 16,
        paddingVertical: 14,
    },
    left: {
        flexDirection: "row",
        alignItems: "center",
        gap: 12,
    },
    label: {
        fontSize: 16,
        fontWeight: "500",
    },
    pressed: {
        opacity: 0.5,
    },
});
