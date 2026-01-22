import NoTransport from "@/components/NoTransport";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import Perdu from "../components/Perdu";
import { Colors } from "../constants/Colors";

export default function HelpScreenUser() {
    const router = useRouter();

    return (
        <SafeAreaView style={styles.safe}>
            <View style={styles.header}>
                <Pressable
                    onPress={() => router.back()}
                    hitSlop={12}
                >
                    <Ionicons
                        name="arrow-back"
                        size={28}
                        color="white"
                    />
                </Pressable>
                <Text style={styles.title}>Que se passe-t-il?</Text>
            </View>
            <View style={styles.content}>
                <Perdu />
                <NoTransport />
            </View>
        </SafeAreaView>
    );
}
const styles = StyleSheet.create({
    safe: {
        flex: 1,
        backgroundColor: Colors.background,
    },
    header: {
        paddingHorizontal: 16,
        paddingTop: 8,
        backgroundColor: Colors.danger,
    },
    content: {
        flex: 1,
        padding: 24,
    },
    title: {
        marginTop: 18,
        fontSize: 26,
        fontWeight: "800",
        marginBottom: 18,
        textAlign: "center",
        color: "#fff"
    },
});

