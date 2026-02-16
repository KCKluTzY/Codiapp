import NoTransport from "@/components/NoTransport";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { Pressable, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import Panne from "../components/Panne";
import Perdu from "../components/Perdu";
import { Colors } from "../constants/Colors";
import ChangerTransport from "@/components/ChangerTransport";
import Blesse from "@/components/Blesse";
import Derange from "@/components/Derange";
import Autre from "@/components/Autre";

export default function HelpScreenUser() {
    const router = useRouter();

    return (
        <SafeAreaView style={styles.safe}>
            <View style={styles.header}>
                <Pressable
                    onPress={() => router.push("/home/HomeScreenUser")}
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
                {/* Présentation des options d'incident (composants réutilisables) */}
                <Perdu />
                <NoTransport />
                <Panne />
                <ChangerTransport />
                <Blesse />
                <Derange />
                <Autre />
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

