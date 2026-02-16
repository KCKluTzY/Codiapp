import { Colors } from "@/constants/Colors";
import { StyleSheet, Text, View, Image, Pressable } from "react-native";
import { useRouter } from "expo-router";
export default function NoTransport() {
    const router = useRouter();
    return (
        // Option 'Transport manquant' : ouvre le chat pour signaler un retard
        <Pressable onPress={() => router.push("/ChatScreen")}>
            <View style={styles.noTransportCard}>
                <Image source={require("../assets/images/noTransport.png")} style={styles.logo} />
                <View style={styles.noTransportTextContainer}>
                    <Text style={styles.noTransportText}>Mon transport n'est pas arrivé</Text>
                </View>
            </View>
        </Pressable>
    )
}
const styles = StyleSheet.create({
    noTransportCard: {
        backgroundColor: Colors.button_background,
        borderRadius: 24,
        padding: 15,
        borderColor: Colors.button_border,
        borderWidth: 5,
        flexDirection: "row",
        alignItems: "center",
        marginBottom: 10,
    },
    noTransportTextContainer: {
        flex: 1,
    },
    noTransportText: {
        color: "#000000",
        fontSize: 18,
        fontWeight: "500",
        marginRight: 10,
    },
    logo: {
        width: 40,
        height: 40,
        marginRight: 10,
    },
});