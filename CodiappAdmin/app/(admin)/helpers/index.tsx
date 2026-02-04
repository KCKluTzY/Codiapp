import { FlatList, View, Text, StyleSheet, Pressable } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";

import AdminHelperCard from "@/components/AdminHelperCard";
import { Helper } from "@/types/Helper";

const HELPERS: Helper[] = [
    {
        id: "1",
        name: "Marie Dupont",
        maxDistance: 10,
        status: "available",
        helpsThisMonth: 8,
    },
    {
        id: "2",
        name: "Lucas Martin",
        maxDistance: 5,
        status: "unavailable",
        helpsThisMonth: 2,
    },
];

export default function HelpersScreen() {
    const router = useRouter();

    return (
        <SafeAreaView style={{ flex: 1 }}>
            {/* Header */}
            <View style={styles.header}>
                <Pressable onPress={() => router.back()} hitSlop={12}>
                    <Ionicons name="arrow-back" size={26} color="#111" />
                </Pressable>

                <Text style={styles.title}>Gérer les aidants</Text>

                {/* Spacer pour centrer le titre */}
                <View style={{ width: 26 }} />
            </View>

            {/* Liste */}
            <FlatList
                data={HELPERS}
                keyExtractor={(item) => item.id}
                renderItem={({ item }) => (
                    <AdminHelperCard helper={item} />
                )}
                contentContainerStyle={styles.list}
                ItemSeparatorComponent={() => <View style={{ height: 12 }} />}
            />
        </SafeAreaView>
    );
}

const styles = StyleSheet.create({
    header: {
        flexDirection: "row",
        alignItems: "center",
        justifyContent: "space-between",
        paddingHorizontal: 16,
        paddingBottom: 12,
    },
    title: {
        fontSize: 18,
        fontWeight: "700",
    },
    list: {
        padding: 16,
    },
});
