import { View, Text, StyleSheet, Pressable } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";

interface Props {
    helperId?: string;
}

export default function HelperHeader({ helperId }: Props) {
    const router = useRouter();

    return (
        <View style={styles.container}>
            <Pressable onPress={() => router.back()} hitSlop={12}>
                <Ionicons name="arrow-back" size={26} color="white" />
            </Pressable>

            <Text style={styles.title}>Profil aidant</Text>

            <View style={{ width: 26 }} />
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        backgroundColor: "#4db5ff",
        paddingTop: 48,
        paddingBottom: 24,
        paddingHorizontal: 16,
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
        borderBottomLeftRadius: 24,
        borderBottomRightRadius: 24,
    },
    title: {
        color: "white",
        fontSize: 18,
        fontWeight: "700",
    },
});
