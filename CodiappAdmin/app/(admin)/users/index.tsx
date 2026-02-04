import { FlatList, View, Text, StyleSheet, Pressable } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";
import { useRouter } from "expo-router";

import AdminUserCard from "@/components/AdminUserCard";
import { User } from "@/types/User";

const USERS: User[] = [
    {
        id: "1",
        name: "Thomas L.",
        age: 32,
        city: "Valenciennes",
        status: "active",
        tutor: "Marie Dupont",
    },
    {
        id: "2",
        name: "Sophie M.",
        age: 36,
        city: "Aulnoy-Lez-Valenciennes",
        status: "waiting",
        tutor: null,
    },
];

export default function UsersScreen() {
    const router = useRouter();

    return (
        <SafeAreaView style={{ flex: 1 }}>
            {/* Header */}
            <View style={styles.header}>
                <Pressable onPress={() => router.back()} hitSlop={12}>
                    <Ionicons name="arrow-back" size={26} color="#111" />
                </Pressable>

                <Text style={styles.title}>Gérer les aidés</Text>

                {/* Spacer pour centrer le titre */}
                <View style={{ width: 26 }} />
            </View>

            {/* Liste */}
            <FlatList
                data={USERS}
                keyExtractor={(item) => item.id}
                renderItem={({ item }) => <AdminUserCard user={item} />}
                contentContainerStyle={styles.list}
                showsVerticalScrollIndicator={false}
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
        paddingBottom: 24,
    },
});
