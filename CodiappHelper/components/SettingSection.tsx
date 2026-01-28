import { View, Text, StyleSheet } from "react-native";
import { ReactNode } from "react";

interface SettingSectionProps {
    title: string;
    children: ReactNode;
}

export default function SettingSection({
    title,
    children,
}: SettingSectionProps) {
    return (
        <View style={styles.container}>
            <Text style={styles.title}>{title}</Text>
            <View style={styles.content}>{children}</View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        marginBottom: 24,
    },
    title: {
        fontSize: 14,
        fontWeight: "700",
        color: "#666",
        marginBottom: 8,
        paddingHorizontal: 16,
    },
    content: {
        backgroundColor: "#fff",
        borderRadius: 16,
        paddingVertical: 8,
        overflow: "hidden",
    },
});
