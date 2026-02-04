import { ScrollView } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { useLocalSearchParams } from "expo-router";

import UserHeader from "@/components/UserHeader";
import UserInfoCard from "@/components/UserInfoCard";
import UserActions from "@/components/UserAction";

export default function AdminUserDetail() {
    const { id } = useLocalSearchParams<{ id: string }>();

    return (
        <SafeAreaView style={{ flex: 1 }} edges={["top"]}>
            <ScrollView contentContainerStyle={{ paddingBottom: 24 }}>
                <UserHeader />
                <UserInfoCard userId={id} />
                <UserActions userId={id} />
            </ScrollView>
        </SafeAreaView>

    );
}
