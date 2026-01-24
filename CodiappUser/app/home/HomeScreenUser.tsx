import { Image, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import DailyRoad from "../../components/DailyRoad";
import EmergencyButton from "../../components/EmergencyButton";
import StatusCard from "../../components/StatusCard";
import { Colors } from "../../constants/Colors";
import ChangerTransport from "@/components/ChangerTransport";

export default function HomeScreenUser() {
    return (
        <SafeAreaView style={{ flex: 1 }}>
            <ScrollView style={styles.container}>
                <View style={styles.logoContainer}>
                    <Image source={require("../../assets/images/logo.png")} style={styles.logo} />
                </View>
                <Text style={styles.title}>Codi App</Text>
                <StatusCard />
                <EmergencyButton />
                <DailyRoad />
            </ScrollView>
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: Colors.background,
        padding: 16,
    },
    title: {
        fontSize: 22,
        fontWeight: "700",
        marginBottom: 16,
        textAlign: "center",
        color: "#36029d"
    },
    logo: {
        width: 100,
        height: 100,
    },
    logoContainer: {
        alignItems: "center",
        marginBottom: 0.2,
    },
})